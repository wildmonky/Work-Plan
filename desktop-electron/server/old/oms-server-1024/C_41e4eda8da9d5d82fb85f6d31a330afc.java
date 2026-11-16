import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_41e4eda8da9d5d82fb85f6d31a330afc
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
         return CommonFunc.getSendtoDept(mapRow);
    }
}
