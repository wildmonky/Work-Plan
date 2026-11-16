import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_3bae5607d6630093efa7f9ff38a8ec99
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return  CommonFunc.unix2Date(CommonFunc.unixTimeStr());
    }
}
