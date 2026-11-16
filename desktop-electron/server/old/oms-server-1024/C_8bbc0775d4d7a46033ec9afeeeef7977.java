import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_8bbc0775d4d7a46033ec9afeeeef7977
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "1".equals(mapRow.get("MODIFY_FLAG_MONTH"))?"<font color=red>"+mapRow.get("MONTH_OCS_DL")+"</font>":"mapRow.get("MONTH_OCS_DL")";
    }
}
