import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_fd7e77c73e09510a3aafbe05fa1f4cf6
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.unix2Date().get(mapRow.get("CREATE_DATE"));

    }
}
